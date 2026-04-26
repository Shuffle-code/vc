package tt.chat.vc.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import tt.chat.vc.entity.Address;

public interface AddressDao extends JpaRepository<Address, Long> {
}
