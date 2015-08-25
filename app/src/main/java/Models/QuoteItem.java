package Models;

import java.io.Serializable;

/**
 * Created by Cesar on 12/08/15.
 */
public class QuoteItem implements Serializable {
    public String description;
    public float amount, subtotal;

    public  QuoteItem() {

    }

    public QuoteItem(float amount, String description, float subtotal) {
        this.amount = amount;
        this.description = description;
        this.subtotal = subtotal;
    }

}
