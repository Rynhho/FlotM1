
/**
 * Manages communication between disk and application.
 */
public class Parser {
    /**
     * Reads an instance from the disk.
     * 
     * @param filepath The filepath to the instance file.
     * 
     * @return An Instance object corresponding to the instance on the file.
     */
    Instance readInstance(String filepath) {
        Instance instance = new Instance();

        return instance;
    }

    /**
     * Reads a solution from the disk.
     * 
     * @param filepath The filepath to the solution file.
     * 
     * @return A Solution object corresponding to the solution on the file.
     */
    Solution readSolution(String filepath) {
        Solution solution = new Solution();

        return solution;
    }

    /**
     * Saves an Instance object to the disk, overriding any existing file.
     * 
     * @param filepath The file to be written to save the instance.
     * @param instance The instance to save.
     */
    void saveInstance(String filepath, Instance instance) {

    }

    /**
     * Saves a Solution object to the disk, overriding any existing file.
     * 
     * @param filepath The file to be written to save the solution.
     * @param instance The solution to save.
     */
    void saveSolution(String filepath, Solution solution) {

    }
}
