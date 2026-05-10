package cz.project_storage.service;

import cz.project_storage.model.Coffee;
import cz.project_storage.repository.CoffeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CoffeeService {

    @Autowired
    private CoffeeRepository coffeeRepository;

    public List<Coffee> getAllCoffees() {
        return coffeeRepository.findAll();
    }

    public void save(Coffee coffee) {
        coffeeRepository.save(coffee);
    }

    public Coffee getById(Long id) {
        return coffeeRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        coffeeRepository.deleteById(id);
    }
}