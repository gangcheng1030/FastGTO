package gangcheng1030.texasholdem.fastgto.dao;

import gangcheng1030.texasholdem.fastgto.po.Postflop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostflopRepository extends JpaRepository<Postflop, Integer> {
    Postflop findFirstByFlopCardsAndParentAndPreflopActions(String flopCards, Integer parent, String preflopActions);
}
