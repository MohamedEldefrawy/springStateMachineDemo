package guru.springframework.msscssm.interceptor;

import guru.springframework.msscssm.domain.Payment;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.repository.PaymentRepository;
import guru.springframework.msscssm.util.PaymentStateMachineUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStateInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {
  private final PaymentRepository paymentRepository;

  @Override
  public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message, Transition<PaymentState, PaymentEvent> transition,
      StateMachine<PaymentState, PaymentEvent> stateMachine) {
    Optional.ofNullable((Long) message.getHeaders().getOrDefault(PaymentStateMachineUtil.PAYMENT_ID_HEADER, -1L))
        .ifPresent(paymentId -> {
          Payment payment = this.paymentRepository.getOne(paymentId);
          payment.setPaymentState(state.getId());
          this.paymentRepository.save(payment);
        });

  }
}
