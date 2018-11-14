package org.lwt.dao;

import java.util.ArrayList;
import java.util.List;

import org.lwt.entity.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationRepository extends JpaRepository<Vacation, Integer> {

    public List<Vacation> findByUserId(String userId);
}
