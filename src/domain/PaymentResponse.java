package domain;

public class PaymentResponse implements Response{
    private final PaymentStatus status;

    public PaymentResponse(PaymentStatus status) {
        this.status = status;
    }
    public PaymentStatus getStatus() {
        return status;
    }
}
