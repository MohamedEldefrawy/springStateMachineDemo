package guru.springframework.msscssm.config;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import java.util.EnumSet;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;

@Configuration
@EnableStateMachine
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {
  @SneakyThrows
  @Override
  public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) {
    states.withStates().initial(PaymentState.NEW)
        .states(EnumSet.allOf(PaymentState.class))
        .end(PaymentState.AUTHORIZE)
        .end(PaymentState.AUTHORIZE_ERROR)
        .end(PaymentState.PRE_AUTHORIZE_ERROR);
  }
}
