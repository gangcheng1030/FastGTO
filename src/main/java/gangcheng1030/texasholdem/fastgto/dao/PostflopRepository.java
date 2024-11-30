package gangcheng1030.texasholdem.fastgto.dao;

import gangcheng1030.texasholdem.fastgto.po.Postflop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostflopRepository extends JpaRepository<Postflop, Long> {
    Postflop findFirstByFlopCardsAndParentAndPreflopActions(String flopCards, Long parent, String preflopActions);
    Postflop findFirstByFlopCardsAndParentAndAction(String flopCards, Long parent, String action);
}
