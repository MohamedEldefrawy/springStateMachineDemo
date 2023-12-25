package guru.springframework.msscssm.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import guru.springframework.msscssm.domain.Payment;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import java.math.BigDecimal;
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


  private Payment createPayment(BigDecimal amount) {
    Payment payment = Payment.builder().amount(amount).build();
    return this.paymentService.newPayment(payment);
  }

  @Test
  void testApprovePreAuth() {
    Payment payment = createPayment(new BigDecimal(100));
    paymentService.preAuth(payment.getId());
    paymentService.approvePreAuth(payment.getId());
    payment = paymentService.getPayment(payment.getId());
    assertEquals(payment.getPaymentState(), stateMachine.getState().getId());
  }

  @Test
  void testDeclinePreAuth() {
    Payment payment = createPayment(new BigDecimal(100));
    paymentService.preAuth(payment.getId());
    paymentService.declinePreAuth(payment.getId());
    payment = paymentService.getPayment(payment.getId());
    assertEquals(payment.getPaymentState(), stateMachine.getState().getId());
  }

  @Test
  void testApproveAuth() {
    Payment payment = createPayment(new BigDecimal(200));
    paymentService.preAuth(payment.getId());
    paymentService.approvePreAuth(payment.getId());
    paymentService.auth(payment.getId());
    paymentService.approveAuth(payment.getId());
    payment = paymentService.getPayment(payment.getId());
    assertEquals(payment.getPaymentState(), stateMachine.getState().getId());
  }

  @Test
  void testDeclineAuth() {
    Payment payment = createPayment(new BigDecimal(200));
    paymentService.preAuth(payment.getId());
    paymentService.approvePreAuth(payment.getId());
    paymentService.auth(payment.getId());
    paymentService.declineAuth(payment.getId());
    payment = paymentService.getPayment(payment.getId());
    assertEquals(payment.getPaymentState(), stateMachine.getState().getId());
  }

}