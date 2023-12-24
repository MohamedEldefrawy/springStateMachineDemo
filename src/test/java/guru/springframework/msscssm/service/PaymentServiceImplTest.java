package guru.springframework.msscssm.service;

import guru.springframework.msscssm.domain.Payment;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

@SpringBootTest
class PaymentServiceImplTest {

  @Autowired
  private PaymentService paymentService;
  @Autowired
  private StateMachine<PaymentState, PaymentEvent> stateMachine;

  @Test
  void testPreAuth() {
    Payment payment = paymentService.newPayment(Payment.builder().amount(new BigDecimal(100)).id(1L).build());
    paymentService.preAuth(payment.getId());
    Assertions.assertEquals(paymentService.getPayment(payment.getId()).getPaymentState(), stateMachine.getState().getId());
  }

  @Test
  void testPreAuthApproved() {
    Payment payment = paymentService.newPayment(Payment.builder().amount(new BigDecimal(100)).build());
    paymentService.preAuth(payment.getId());
    paymentService.approvePreAuth(payment.getId());
    Assertions.assertEquals(paymentService.getPayment(payment.getId()).getPaymentState(), stateMachine.getState().getId());
  }

}