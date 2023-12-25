package guru.springframework.msscssm.service;

import guru.springframework.msscssm.domain.Payment;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.interceptor.PaymentStateInterceptor;
import guru.springframework.msscssm.repository.PaymentRepository;
import guru.springframework.msscssm.util.PaymentStateMachineUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
  private final PaymentRepository paymentRepository;
  private final Logger logger;
  private final PaymentStateInterceptor paymentStateInterceptor;
  private final StateMachine<PaymentState, PaymentEvent> stateMachine;

  @Override
  public Payment newPayment(Payment payment) {
    payment.setPaymentState(PaymentState.NEW);
    return this.paymentRepository.save(payment);
  }

  @Override
  public Payment getPayment(Long paymentId) {
    return this.paymentRepository.findById(paymentId).get();
  }

  @Override
  public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
    buildStateMachine(PaymentState.NEW);
    sendEvent(paymentId, this.stateMachine, PaymentEvent.PRE_AUTHORIZE);
    return null;
  }

  @Override
  public StateMachine<PaymentState, PaymentEvent> declinePreAuth(Long paymentId) {
    buildStateMachine(PaymentState.NEW);
    sendEvent(paymentId, this.stateMachine, PaymentEvent.PRE_AUTHORIZE_DECLINED);
    return null;
  }

  @Override
  public StateMachine<PaymentState, PaymentEvent> approvePreAuth(Long paymentId) {
    buildStateMachine(PaymentState.NEW);
    sendEvent(paymentId, this.stateMachine, PaymentEvent.PRE_AUTHORIZE_APPROVED);
    return null;
  }

  @Override
  public StateMachine<PaymentState, PaymentEvent> auth(Long paymentId) {
    buildStateMachine(PaymentState.PRE_AUTHORIZE);
    sendEvent(paymentId, this.stateMachine, PaymentEvent.AUTHORIZE);
    return null;
  }

  @Override
  public StateMachine<PaymentState, PaymentEvent> approveAuth(Long paymentId) {
    buildStateMachine(PaymentState.PRE_AUTHORIZE);
    sendEvent(paymentId, this.stateMachine, PaymentEvent.AUTHORIZE_APPROVED);
    return null;
  }

  @Override
  public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
    buildStateMachine(PaymentState.PRE_AUTHORIZE);
    sendEvent(paymentId, this.stateMachine, PaymentEvent.AUTHORIZE_DECLINED);
    return null;
  }

  private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> stateMachine, PaymentEvent event) {
    Message<PaymentEvent> message = MessageBuilder.withPayload(event).setHeader(PaymentStateMachineUtil.PAYMENT_ID_HEADER, paymentId).build();
    stateMachine.sendEvent(message);
  }

  private void buildStateMachine(PaymentState paymentState) {
    this.stateMachine.stop();
    this.stateMachine.getStateMachineAccessor().doWithAllRegions(
        paymentStatePaymentEventStateMachineAccess -> {
          paymentStatePaymentEventStateMachineAccess.resetStateMachine(
              new DefaultStateMachineContext<>(paymentState, null, null, null));
          paymentStatePaymentEventStateMachineAccess.addStateMachineInterceptor(this.paymentStateInterceptor);
        }
    );
    this.stateMachine.start();
  }
}