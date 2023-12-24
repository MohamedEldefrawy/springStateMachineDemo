package guru.springframework.msscssm.service;

import guru.springframework.msscssm.domain.Payment;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {
  public Payment newPayment(Payment payment);

  public Payment getPayment(Long paymentId);

  public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);

  public StateMachine<PaymentState, PaymentEvent> declinePreAuth(Long paymentId);

  public StateMachine<PaymentState, PaymentEvent> approvePreAuth(Long paymentId);

  public StateMachine<PaymentState, PaymentEvent> auth(Long paymentId);

  public StateMachine<PaymentState, PaymentEvent> approveAuth(Long paymentId);

  public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId);

}
