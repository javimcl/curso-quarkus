package org.digitalthinking.entites;

import com.blazebit.persistence.view.EntityView;

@EntityView(Customer.class)
public interface CustomerView {

    Long getId();

    String getAccountNumber();
}
