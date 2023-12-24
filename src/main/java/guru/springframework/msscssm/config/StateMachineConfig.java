package guru.springframework.msscssm.config;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import java.util.EnumSet;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;

@Configuration
@EnableStateMachine
public class StateMachineConfig {

  @SneakyThrows
  @Bean
  public StateMachine<PaymentState, PaymentEvent> stateMachine() {
    StateMachineBuilder.Builder<PaymentState, PaymentEvent> builder = StateMachineBuilder.builder();

    builder.configureStates().
        withStates().initial(PaymentState.NEW) // Define Initial state
        .states(EnumSet.allOf(PaymentState.class)) // Define states
        .end(PaymentState.AUTHORIZE)  // Define Termination states
        .end(PaymentState.AUTHORIZE_ERROR)
        .end(PaymentState.PRE_AUTHORIZE_ERROR);

    builder.configureTransitions()
        .withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE)
        .and().withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTHORIZE).event(PaymentEvent.PRE_AUTHORIZE_APPROVED)
        .and().withExternal().source(PaymentState.PRE_AUTHORIZE).target(PaymentState.AUTHORIZE).event(PaymentEvent.AUTHORIZE_APPROVED)
        .and().withExternal().source(PaymentState.PRE_AUTHORIZE).target(PaymentState.PRE_AUTHORIZE).event(PaymentEvent.AUTHORIZE)
        .and().withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTHORIZE_ERROR).event(PaymentEvent.PRE_AUTHORIZE_DECLINED);

    return builder.build();
  }
}
