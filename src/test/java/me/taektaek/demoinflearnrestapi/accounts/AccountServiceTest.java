package me.taektaek.demoinflearnrestapi.accounts;

import lombok.ToString;
import org.junit.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    @DisplayName("인증 테스트")
    public void findByUsername() {
        //Given
        String password = "intaek";
        String username = "intaek428@gmail.com";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountRepository.save(account);

        //When
        UserDetailsService userDetailsService = (UserDetailsService) accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        //Then
        assertThat(userDetails.getPassword()).isEqualTo(password);
    }

    @Test
    @DisplayName("해당 아이디가 없을 경우 오류가 나는 예외 테스트")
    public void findByUsernameFail() {
        String username = "random@email.com";
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> accountService.loadUserByUsername(username));
        assertThat(exception).hasMessageContaining(username);
    }



}