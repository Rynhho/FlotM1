package optim.flow.domain;

public interface SolutionRepo {
    /**
     * Saves a solution into some kind of adapted media.
     * 
     * @param solution The solution to save.
     * 
     * @return The ID given to the saved solution.
     */
    String save(Solution solution);

    /**
     * Saves a solution into some kind of adapted media, with the given ID.
     * 
     * @param solution The solution to save.
     * @param ID       The solution's ID.
     */
    void save(Solution solution, String ID);

    /**
     * Loads a solution from some kind of adapted media.
     * 
     * @param ID The solution's ID.
     * 
     * @return The Solution object corresponding to the ID.
     */
    Solution load(String ID);
}
