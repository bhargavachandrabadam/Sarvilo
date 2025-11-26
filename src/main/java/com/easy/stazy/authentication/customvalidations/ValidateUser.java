package com.easy.stazy.authentication.customvalidations;

import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.authentication.entities.UsersEntity;

public interface ValidateUser {

    UsersEntity validMobileNumber(String mobileNumber);

    PgOwnerEntity validatePgOwner(String mobileNumber);
}
