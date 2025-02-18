package services.data.dto;

import java.math.BigDecimal;
import java.util.Set;

public class CreateDispatcherInputModel extends CreateEmployeeInputModel {

    private Set<Long> supervisedDriverIds;

    // Constructor
    public CreateDispatcherInputModel() {}

    public CreateDispatcherInputModel(String firstName, String familyName,
                                      BigDecimal salary, Long transportCompanyId,
                                      Set<Long> supervisedDriverIds) {
        super(firstName, familyName, salary, transportCompanyId);
        this.supervisedDriverIds = supervisedDriverIds;
    }

    public Set<Long> getSupervisedDriverIds() {
        return supervisedDriverIds;
    }

    public void setSupervisedDriverIds(Set<Long> supervisedDriverIds) {
        this.supervisedDriverIds = supervisedDriverIds;
    }

}
