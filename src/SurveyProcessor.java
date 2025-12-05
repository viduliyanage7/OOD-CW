import java.util.concurrent.Callable;

public class SurveyProcessor implements Callable<Integer> {

    private final int q1, q2, q3, q4, q5;

    public SurveyProcessor(int q1, int q2, int q3, int q4, int q5) {
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        this.q4 = q4;
        this.q5 = q5;
    }

    @Override
    public Integer call() {
        try {
            Thread.sleep(500);

            int score = (q1 + q2 + q3 + q4 + q5) * 4;
            System.out.println("✔ Survey processed in background (Score = " + score + ")");
            return score;

        } catch (InterruptedException e) {
            return 0;
        }
    }
}
