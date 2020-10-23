package up.edu.pandemicgamestate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button testButton = findViewById(R.id.runtestbutton);
        testButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        EditText text = (EditText) findViewById(R.id.tostringarea);
        //clear previous inputs
        text.setText("");

        //create first instance of game state
        PandemicState firstInstance = new PandemicState(2, true);
        //use copy constructor for secondInstance
        PandemicState secondInstance = new PandemicState(firstInstance);


        //call all methods using firstInstance
        firstInstance.share(0);
        text.setText(text.getText() + "Player One shared atlanta with Player Two\n");
        firstInstance.driveFerry(0, firstInstance.getDeck().getCity("Washington"));
        text.setText(text.getText() + "Player One drove/ferry to Washington\n");
        firstInstance.buildStation(0);
        text.setText(text.getText() + "Built research station on Washington, discards Washington" +
                " from hand\n");
        firstInstance.directFlight(0, firstInstance.getDeck().getCity("Chicago"));
        text.setText(text.getText() + "Player One took direct flight to Chicago, discarding Chicago " +
                "card from hand\n");
        firstInstance.endTurn(0);
        text.setText(text.getText() + "Ended Player 1 turn\n");

        firstInstance.cure(1);
        text.setText(text.getText() + "Player 2 cured blue, discarding all 5 cards from hand\n");
        firstInstance.shuttleFlight(1, firstInstance.getDeck().getCity("Washington"));
        text.setText(text.getText() + "Player 2 took shuttle flight to Washington, " +
                "research station to research station, no cards discarded\n");
        firstInstance.driveFerry(1, firstInstance.getDeck().getCity("Miami"));
        text.setText(text.getText() + "Player 2 drove/ferry to Miami\n");
        firstInstance.forgoAction(1);
        text.setText(text.getText() + "Player Two forgoed an action\n");
        firstInstance.endTurn(1);
        text.setText(text.getText() + "Player Two ended their turn\n");

        firstInstance.driveFerry(0, firstInstance.getDeck().getCity("San Francisco"));
        text.setText(text.getText() + "Player One moved to San Francisco\n");
        firstInstance.charterFlight(0, firstInstance.getDeck().getCity("Paris"));
        text.setText(text.getText() + "Player One discarded the Paris card to charter to Paris\n");
        firstInstance.treat(0);
        text.setText(text.getText() + "Player One treated a cube at Paris\n");
        firstInstance.forgoAction(0);
        text.setText(text.getText() + "Player One forgoed an action\n");
        firstInstance.endTurn(0);
        text.setText(text.getText() + "Player One ended their turn\n");

        //create thirdInstance using default constructor
        PandemicState thirdInstance = new PandemicState(2, true);
        //create fourthInstance using copy constructor
        PandemicState fourthInstance = new PandemicState(thirdInstance);

        //call toString() using secondInstance and fourthInstance
        text.setText(text.getText() + secondInstance.toString());
        text.setText(text.getText() + fourthInstance.toString());
    }
}