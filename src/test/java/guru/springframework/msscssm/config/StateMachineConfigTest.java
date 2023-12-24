package guru.springframework.msscssm.config;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

@SpringBootTest
class StateMachineConfigTest {

  @Autowired
  private StateMachine<PaymentState, PaymentEvent> stateMachine;
  @Autowired
  private Logger logger;

  @Test
  void testNewStateMachine() {
    stateMachine.start();
    stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE);
    stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE_APPROVED);
    stateMachine.sendEvent(PaymentEvent.AUTHORIZE);
    stateMachine.sendEvent(PaymentEvent.AUTHORIZE_APPROVED);
  }
}