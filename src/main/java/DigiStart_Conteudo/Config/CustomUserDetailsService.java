package DigiStart_Conteudo.Config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    .password("$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi") // password: "password"
                    .roles("ADMIN")
                    .build();
        } else if ("professor".equals(username)) {
            return User.builder()
                    .username("professor")
                    .password("$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi") // password: "password"
                    .roles("PROFESSOR")
                    .build();
        } else if ("aluno".equals(username)) {
            return User.builder()
                    .username("aluno")
                    .password("$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi") // password: "password"
                    .roles("ALUNO")
                    .build();
        }
        
        throw new UsernameNotFoundException("Usuário não encontrado: " + username);
    }
}
