package mx.edu.tesoem.isc.karm.FireBase;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mx.edu.tesoem.isc.karm.FireBase.model.Persona;

public class MainActivity extends AppCompatActivity {
    private List<Persona> listPerson = new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;

    EditText name, lastname, email, pass;
    ListView listpersona;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Persona perSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.txtname);
        lastname = findViewById(R.id.txtlastname);
        email = findViewById(R.id.txtemail);
        pass = findViewById(R.id.txtpass);

        listpersona = findViewById(R.id.lvdatos);
        iniciarFirebase();
        listaDato();

        listpersona.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                perSelect = (Persona) parent.getItemAtPosition(position);
                name.setText(perSelect.getNombre());
                lastname.setText(perSelect.getApellido());
                email.setText(perSelect.getEmail());
                pass.setText(perSelect.getPassword());
            }
        });
    }

    private void listaDato() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPerson.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Persona p = objSnapshot.getValue(Persona.class);
                    listPerson.add(p);

                    arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1, listPerson);
                    listpersona.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String nombre = name.getText().toString();
        String ape = lastname.getText().toString();
        String correo = email.getText().toString();
        String passw = pass.getText().toString();
        switch (item.getItemId()){
            case R.id.icon_add:{
                if (nombre.equals("")){
                    validacion();
                }
                else {
                    Persona p = new Persona();
                    p.setUid(UUID.randomUUID().toString());
                    p.setNombre(nombre);
                    p.setApellido(ape);
                    p.setEmail(correo);
                    p.setPassword(passw);
                    databaseReference.child("Persona").child(p.getUid()).setValue(p);
                    Toast.makeText(this, "Agregago", Toast.LENGTH_LONG).show();
                    limpiar();
                }
                break;
            }
            case R.id.icon_save:{
                AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                alerta.setMessage("¿Deseas actualizar los datos?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Persona p = new Persona();
                                p.setUid(perSelect.getUid());
                                p.setNombre(name.getText().toString().trim());
                                p.setApellido(lastname.getText().toString().trim());
                                p.setEmail(email.getText().toString().trim());
                                p.setPassword(pass.getText().toString().trim());
                                databaseReference.child("Persona").child(p.getUid()).setValue(p);
                                limpiar();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog tituto = alerta.create();
                tituto.setTitle("Actualizar");
                tituto.show();
                break;
            }
            case R.id.icon_delete:{
                AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                alerta.setMessage("¿Deseas eliminar los datos?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Persona p = new Persona();
                                p.setUid(perSelect.getUid());
                                databaseReference.child("Persona").child(p.getUid()).removeValue();
                                limpiar();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog tituto = alerta.create();
                tituto.setTitle("Eliminar");
                tituto.show();
                break;
            }
            default:break;
        }
        return true;
    }

    private void limpiar() {
        name.setText("");
        lastname.setText("");
        email.setText("");
        pass.setText("");
    }

    private void validacion() {
        String nombre = name.getText().toString();
        String ape = lastname.getText().toString();
        String correo = email.getText().toString();
        String passw = pass.getText().toString();

        if (nombre.equals("")){
            name.setError("Requerido");
        }
        else if (ape.equals("")){
            lastname.setError("Requerido");
        }
        else if (correo.equals("")){
            email.setError("Requerido");
        }
        else if (passw.equals("")){
            pass.setError("Requerido");
        }
    }
}
