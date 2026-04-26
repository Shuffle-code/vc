package tt.chat.vc.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tt.chat.vc.dao.ObserverImageDao;
import tt.chat.vc.dao.security.AccountRoleDao;
import tt.chat.vc.dao.security.AccountUserDao;
import tt.chat.vc.dao.security.ConfirmationCodeDao;
import tt.chat.vc.dto.UserDto;
//import tt.chat.vc.dto.mapper.UserMapper;
import tt.chat.vc.entity.Observer;
import tt.chat.vc.entity.ObserverImage;
import tt.chat.vc.entity.enums.Status;
import tt.chat.vc.entity.security.AccountRole;
import tt.chat.vc.entity.security.AccountUser;
import tt.chat.vc.entity.security.ConfirmationCode;
import tt.chat.vc.entity.security.enums.AccountStatus;
import tt.chat.vc.exception.UsernameAlreadyExistsException;
import tt.chat.vc.service.ObserverService;
import tt.chat.vc.service.UserService;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaUserDetailService implements UserDetailsService, UserService {

    private final String imageName = "image104-66.jpg";
    private final AccountUserDao accountUserDao;
    private final AccountRoleDao accountRoleDao;
    public final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationCodeDao confirmationCodeDao;
    private final ObserverImageDao observerImageDao;
    private final ObserverService observerService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(username + " name");
        return accountUserDao.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username: " + username + " not found")
        );

    }

    @Override
    public String getConfirmationCode() {
        String confirmationCode;
        return confirmationCode = RandomStringUtils.randomNumeric(5);
    }

    @Override
    public UserDto register(UserDto userDto) {
        if (accountUserDao.findByUsername(userDto.getUsername()).isPresent()) {
            throw  new UsernameAlreadyExistsException(String.format(
                    "Пользователь с таким логином %s уже существует", userDto.getUsername()));
        }
        AccountUser accountUser = modelMapper.map(userDto, AccountUser.class);
        Observer observer = addNewObserver(accountUser);
//        player.setRating(BigDecimal.valueOf(500));
        observerService.saveNew(observer);
        ObserverImage observerImage = addNewImage(imageName, observer);
        log.info(observerImage.getId().toString());
        observerImage.setId(null);
        observerImageDao.save(observerImage);
        AccountRole roleUser = accountRoleDao.findByName("ROLE_USER");
        AccountRole roleAdmin = accountRoleDao.findByName("ROLE_ADMIN");
        AccountRole roleObserver = accountRoleDao.findByName("ROLE_OBSERVER");
        long count = accountUserDao.count();
        if(count == 0){
            accountUser.setRoles(Set.of(roleAdmin));
        } else accountUser.setRoles(Set.of(roleUser));
        log.info("Count: " + count);
        log.info(String.valueOf(count == 0));
        accountUser.setStatus(AccountStatus.ACTIVE);
        accountUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        accountUser.setObserver(observer);
        AccountUser registeredAccountUser = accountUserDao.save(accountUser);
        log.debug("User with username {} was registered successfully", registeredAccountUser.getUsername());
        return modelMapper.map(registeredAccountUser, UserDto.class);

    }

    public ObserverImage addNewImage(String nameImage, Observer observer){
        ObserverImage observerImage = new ObserverImage();
        if (observerImageDao.count() != 0){
            observerImage.setId(observerImageDao.maxId() + 1);
        }
        observerImage.setPath(nameImage);
        observerImage.setObserver(observer);
        return observerImage;
    }

    public Observer addNewObserver(AccountUser accountUser){
        Observer observer = modelMapper.map(accountUser, Observer.class);
        if (observerService.countAll() != 0){
            observer.setId(observerService.maxId() + 1);
        }
        observer.setStatus(Status.NOT_ACTIVE);
        return observer;
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto) {
        AccountUser user = modelMapper.map(userDto, AccountUser.class);
        if (user.getId() != null) {
            accountUserDao.findById(userDto.getId()).ifPresent(
                    (p) -> {
                        user.setVersion(p.getVersion());
                        user.setStatus(p.getStatus());
                    }
            );
        }
        return modelMapper.map(accountUserDao.save(user), UserDto.class);

    }
    @Override
    public void disconnect(AccountUser accountUser){
//        var userDaoByUsername = accountUserDao.findByUsername(accountUser.getUsername());
        if (accountUser != null && accountUser.getStatus() != AccountStatus.ONLINE){
            accountUser.setStatus(AccountStatus.OFFLINE);
            accountUserDao.save(accountUser);
        }
    };
    @Override
    public List<AccountUser>findAllByStatus(AccountStatus accountStatus){
        return accountUserDao.findAllByStatus(AccountStatus.ONLINE);
    }

    @Override
    public AccountUser findByUsername(String username) {
        return accountUserDao.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username: " + username + " not found")
        );
    }
    public AccountUser update(AccountUser accountUser) {
        if (accountUser.getId() != null) {
            accountUserDao.findById(accountUser.getId()).ifPresent(
                    (user) ->
                    {
                        accountUser.setVersion(user.getVersion());
//                        accountUser.setStatus(AccountStatus.ONLINE);
                    }
            );
        }
        return accountUserDao.save(accountUser);
    }

    @Override
    public List<UserDto> findAll() {
        return null;
    }

    @Override
    public void generateConfirmationCode(UserDto thisUser, String code) {
        ConfirmationCode confirmationCode = ConfirmationCode.builder().
                code(code)
                .accountUser(modelMapper.map(thisUser, AccountUser.class))
                .build();
        confirmationCodeDao.save(confirmationCode);
    }
    @Override
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        return modelMapper.map(accountUserDao.findById(id).orElse(null), UserDto.class);
    }


    @Override
    @Transactional
    public void deleteById(Long id) {
        final AccountUser accountUser = accountUserDao.findById(id).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format("User with id %s not found", id)
                )
        );
        disable(accountUser);
        update(accountUser);
    }

    private void enable(final AccountUser accountUser) {
        accountUser.setStatus(AccountStatus.ACTIVE);
        accountUser.setAccountNonLocked(true);
        accountUser.setAccountNonExpired(true);
        accountUser.setEnabled(true);
        accountUser.setCredentialsNonExpired(true);
    }

    private void disable(final AccountUser accountUser) {
        accountUser.setStatus(AccountStatus.DELETED);
        accountUser.setAccountNonLocked(false);
        accountUser.setAccountNonExpired(false);
        accountUser.setEnabled(false);
        accountUser.setCredentialsNonExpired(false);
    }
}
