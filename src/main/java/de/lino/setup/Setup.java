package de.lino.setup;

import com.google.common.collect.Lists;
import de.lino.setup.error.SetupFinishedException;
import de.lino.setup.error.SetupSleepingException;
import de.lino.setup.status.SetupStatus;

import java.util.List;
import java.util.function.Consumer;

/**
 * Generic class setup being used to set up ab executable system
 */
public class Setup {

    /**
     * Returning current position of steps
     */
    private int currentStep;

    /**
     * SetupStatus referencing on setup status
     */
    private SetupStatus setupStatus;

    /**
     * Cached steps that are already finished
     * LinkedList for faster writing execution
     */
    private final List<Integer> cachedSteps;

    /**
     * Setup constructor initializing setup object and containing all property of setup
     */
    public Setup() {

        this.currentStep = 0;
        this.setupStatus = SetupStatus.SLEEPING;
        this.cachedSteps = Lists.newLinkedList();

    }

    /**
     * Initializing a new and further step for setup
     * @param newStep Consumer<Integer> containing current Step object
     * @return Setup to cascade setup easily
     */
    public Setup nextStep(final Consumer<Integer> newStep) {

        if (this.setupStatus.equals(SetupStatus.SLEEPING))
            throw new SetupSleepingException("@nextStep.sleeping: Setup is not introduced yet");


        if (this.setupStatus.equals(SetupStatus.FINISHED))
            throw new SetupFinishedException("@nextStep.finished: Setup already finished");

        this.cachedSteps.add(this.currentStep++);
        newStep.accept(this.currentStep - 1);

        return this;
    }

    /**
     * Introduce the setup with initialize method and updating status
     * @param execution Command to be performed
     */
    public void introduce(final Runnable execution) {

        if (this.setupStatus.equals(SetupStatus.FINISHED))
            throw new SetupFinishedException("@introduce.finished: Setup already finished");

        this.setupStatus = SetupStatus.RUNNING;
        while (this.setupStatus.equals(SetupStatus.RUNNING)) execution.run();
    }

    /**
     * Finish to set up and updating the setup status
     * @param currentStep Step where to set up was finish at
     */
    public void finish(final Consumer<Integer> currentStep) {

        if (this.setupStatus.equals(SetupStatus.SLEEPING))
            throw new SetupSleepingException("@finish.sleeping: Setup is not introduced yet");

        this.setupStatus = SetupStatus.FINISHED;
        currentStep.accept(this.cachedSteps.get(this.currentStep - 1));
    }

    /**
     * Containing cached steps
     * @return LinkedList<Integer> containing all cached steps
     */
    public List<Integer> getCachedSteps() {
        return cachedSteps;
    }

    /**
     * Get current steps
     * @return current Step
     */
    public int getCurrentStep() {
        return currentStep;
    }

    /**
     * Return status of Setup
     * @return SetupStatus
     */
    public SetupStatus getSetupStatus() {
        return this.setupStatus;
    }

}
