package cox.api.challenge.data_interfaces;

import java.util.ArrayList;
import java.util.List;

public class AddDealer {

    public void addDealer(final Dealers dealer) {
        this.dealers.add(dealer);
    }

    private final List<Dealers> dealers = new ArrayList<>();
}
