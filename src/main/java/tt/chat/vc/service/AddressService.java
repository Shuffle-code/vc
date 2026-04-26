package tt.chat.vc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tt.chat.vc.dao.AddressDao;
import tt.chat.vc.entity.Address;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {
    private final AddressDao addressDao;
    public List<Address> findAll(){
        return addressDao.findAll();
    }
}
