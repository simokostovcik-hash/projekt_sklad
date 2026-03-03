package cz.projekt_sklad.config;

import cz.projekt_sklad.model.Uzivatel;
import cz.projekt_sklad.repository.UzivatelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MojeUzivatelService implements UserDetailsService {

    @Autowired
    private UzivatelRepository uzivatelRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Uzivatel> uzivatelOpt = uzivatelRepository.findByUsername(username);

        if (uzivatelOpt.isEmpty()) {
            throw new UsernameNotFoundException("Uživatel nenalezen: " + username);
        }

        Uzivatel u = uzivatelOpt.get();

        return User.builder()
                .username(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRole().replace("ROLE_", ""))
                .build();
    }
}