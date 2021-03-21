package org.pikIt.studentInit.services;

import org.pikIt.studentInit.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
//    List<User> findNameBy(String name);
}
