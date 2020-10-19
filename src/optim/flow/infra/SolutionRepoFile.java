package optim.flow.infra;

import java.util.Date;

import optim.flow.domain.Solution;
import optim.flow.domain.SolutionRepo;

public class SolutionRepoFile implements SolutionRepo {
    @Override
    public String save(Solution solution) {
        final Date date = new Date();
        final String ID = Long.toString(date.getTime());
        save(solution, ID);

        return ID;
    }

    @Override
    public void save(Solution solution, String ID) {
        // TODO Auto-generated method stub

    }

    @Override
    public Solution load(String ID) {
        // TODO Auto-generated method stub
        return null;
    }
}
