package gangcheng1030.texasholdem.fastgto.dao;

import gangcheng1030.texasholdem.fastgto.po.Preflop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreflopRepository extends JpaRepository<Preflop, Integer> {
    List<Preflop> findByParentAndPosition(Integer parent, String position);
}
