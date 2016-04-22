package edu.gatech.cdcproject.demo.healthrecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import edu.gatech.cdcproject.demo.R;

/**
 * Created by guoweidong on 3/28/16.
 *
 * Edited by CW
 *
 */
public class HealthRecordFragment extends Fragment {
    private Button hRecord;
    public static String serverBase = "http://polaris.i3l.gatech.edu:8080/gt-fhir-webapp/base";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_identify, container, false);

        hRecord = (Button) view.findViewById(R.id.button_0);
        hRecord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FhirContext ctx = new FhirContext();
                IGenericClient client = ctx.newRestfulGenericClient(serverBase);

                //Bundle bundle = client.search().forResource(Patient.class)
                //        .where(Patient.In)




            }
        });

        return inflater.inflate(R.layout.fragment_health_record, container, false);
    }

    private void setupUI() {


    }
}
