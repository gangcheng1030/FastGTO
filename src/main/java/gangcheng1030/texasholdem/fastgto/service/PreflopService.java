package gangcheng1030.texasholdem.fastgto.service;

import gangcheng1030.texasholdem.fastgto.dao.PreflopRepository;
import gangcheng1030.texasholdem.fastgto.po.Preflop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PreflopService {
    @Autowired
    private PreflopRepository preflopRepository;

    public Optional<Preflop> findById(int id) {
        return preflopRepository.findById(id);
    }

    public Preflop save(Preflop preflop) {
        return preflopRepository.save(preflop);
    }

    public List<Preflop> findByParentAndPosition(Integer parent, String position) {
        return preflopRepository.findByParentAndPosition(parent, position);
    }
}
