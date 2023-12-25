package guru.springframework.msscssm.config;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.util.PaymentStateMachineUtil;
import java.util.EnumSet;
import java.util.Random;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Configuration
@EnableStateMachine
public class StateMachineConfig {
  @SneakyThrows
  @Bean
  public StateMachine<PaymentState, PaymentEvent> stateMachine(StateMachineListener<PaymentState, PaymentEvent> listener) {
    StateMachineBuilder.Builder<PaymentState, PaymentEvent> builder = StateMachineBuilder.builder();

    builder.configureStates().
        withStates().initial(PaymentState.NEW) // Define Initial state
        .states(EnumSet.allOf(PaymentState.class)) // Define states
        .end(PaymentState.AUTHORIZE)  // Define Termination states
        .end(PaymentState.AUTHORIZE_ERROR)
        .end(PaymentState.PRE_AUTHORIZE_ERROR);

    builder.configureTransitions()
        .withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE)
        .action(preAuthAction())
        .and().withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTHORIZE).event(PaymentEvent.PRE_AUTHORIZE_APPROVED)
        .and().withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTHORIZE_ERROR).event(PaymentEvent.PRE_AUTHORIZE_DECLINED)
        .and().withExternal().source(PaymentState.PRE_AUTHORIZE).target(PaymentState.PRE_AUTHORIZE).event(PaymentEvent.AUTHORIZE)
        .action(authAction())
        .and().withExternal().source(PaymentState.PRE_AUTHORIZE).target(PaymentState.AUTHORIZE).event(PaymentEvent.AUTHORIZE_APPROVED)
        .and().withExternal().source(PaymentState.PRE_AUTHORIZE).target(PaymentState.AUTHORIZE_ERROR).event(PaymentEvent.AUTHORIZE_DECLINED);
    StateMachine<PaymentState, PaymentEvent> stateMachine = builder.build();
    stateMachine.addStateListener(listener);
    return stateMachine;
  }

  @Bean
  public StateMachineListener<PaymentState, PaymentEvent> listener(Logger logger) {
    return new StateMachineListenerAdapter<PaymentState, PaymentEvent>() {
      @Override
      public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
        logger.info("State changed from: {} to: {}", from, to);
      }
    };
  }

  public Action<PaymentState, PaymentEvent> authAction() {
    return context -> {
      System.out.println("Auth was called!!!");

      if (new Random().nextInt(10) < 8) {
        System.out.println("Auth Approved");
        context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTHORIZE_APPROVED)
            .setHeader(PaymentStateMachineUtil.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentStateMachineUtil.PAYMENT_ID_HEADER))
            .build());

      } else {
        System.out.println("Auth Declined! No Credit!!!!!!");
        context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTHORIZE_DECLINED)
            .setHeader(PaymentStateMachineUtil.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentStateMachineUtil.PAYMENT_ID_HEADER))
            .build());
      }
    };
  }

  public Action<PaymentState, PaymentEvent> preAuthAction() {
    return context -> {
      System.out.println("PreAuth was called!!!");

      if (new Random().nextInt(10) < 8) {
        System.out.println("PreAuth Approved");
        context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTHORIZE_APPROVED)
            .setHeader(PaymentStateMachineUtil.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentStateMachineUtil.PAYMENT_ID_HEADER))
            .build());

      } else {
        System.out.println("PreAuth Declined! No Credit!!!!!!");
        context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTHORIZE_DECLINED)
            .setHeader(PaymentStateMachineUtil.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentStateMachineUtil.PAYMENT_ID_HEADER))
            .build());
      }
    };
  }
}
