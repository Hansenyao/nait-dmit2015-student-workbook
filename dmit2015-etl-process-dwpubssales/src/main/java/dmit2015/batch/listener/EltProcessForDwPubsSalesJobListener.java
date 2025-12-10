package dmit2015.batch.listener;

import jakarta.batch.api.listener.JobListener;
import jakarta.batch.runtime.context.JobContext;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Logger;

/**
 * This listener contains methods that executes before and after a job execution runs.
 * To apply this listener to a batch job you must define a listener element in the Job Specification Language (JSL) file
 * BEFORE the first step element as follows:
 * <pre>{@code
 *
 * <listeners>
 *      <listener ref="eltProcessForDwPubsSalesJobListener" />
 * </listeners>
 *
 * }</pre>
 */
@Named
@Dependent
public class EltProcessForDwPubsSalesJobListener implements JobListener {

    @Inject
    private JobContext jobContext;

    @Inject
    private Logger logger;

    private long startTime;

    @Override
    public void beforeJob() throws Exception {
        logger.info(jobContext.getJobName() + " beforeJob");
        startTime = System.currentTimeMillis();


    }

    @Override
    public void afterJob() throws Exception {
        logger.info(jobContext.getJobName() + "afterJob");
        long endTime = System.currentTimeMillis();
        long durationMilliseconds = (endTime - startTime);
        String message = jobContext.getJobName() + " completed in " + durationMilliseconds + " ms";
        logger.info(message);

    }

}