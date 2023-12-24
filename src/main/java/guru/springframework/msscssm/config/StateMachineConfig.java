package guru.springframework.msscssm.config;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import java.util.EnumSet;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachine
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {
  @SneakyThrows
  @Override
  public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) {
    states.withStates().initial(PaymentState.NEW) // Define Initial state
        .states(EnumSet.allOf(PaymentState.class)) // Define states
        .end(PaymentState.AUTHORIZE)  // Define Termination states
        .end(PaymentState.AUTHORIZE_ERROR)
        .end(PaymentState.PRE_AUTHORIZE_ERROR);
  }

  @SneakyThrows
  @Override
  public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) {
    transitions.withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE)
        .and().withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTHORIZE).event(PaymentEvent.PRE_AUTHORIZE_APPROVED)
        .and().withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTHORIZE_ERROR).event(PaymentEvent.AUTHORIZE_DECLINED);

  }
}
