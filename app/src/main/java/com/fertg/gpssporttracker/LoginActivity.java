package com.fertg.gpssporttracker;

import static android.view.View.INVISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private Button btnlogin, btnRegister, btnClose;
    private EditText login, pass;
    private FrameLayout frameLayoutRegistroC;
    private Fragment myFrag;
    private FragmentTransaction tr;
    private DatabaseReference mDatabase;
    private TextInputLayout txi1, txi2;
    private AwesomeValidation awesomevalidation;
    private FirebaseAuth mAuth;
    String urlT = "https://twitter.com/home";
    String urlF = "https://www.facebook.com/";
    String urlI = "https://www.instagram.com/";
    private InputMethodManager imm;
    private ImageButton tw, inst, fb;
    private ImageView imageLoad, imageLoad2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//btns
        btnlogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegistro);
        //ediText
        login = (EditText) findViewById(R.id.etUsu);
        pass = (EditText) findViewById(R.id.etPass);
        frameLayoutRegistroC = (FrameLayout) findViewById(R.id.frameLayoutRegistro);
        tw = (ImageButton) findViewById(R.id.imTw);
        inst = (ImageButton) findViewById(R.id.imInst);
        fb = (ImageButton) findViewById(R.id.imFac);



//ICONOS DE RRSS CON LISTENERS
        tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNavegador = new Intent(Intent.ACTION_VIEW, Uri.parse(urlT));
                startActivity(intentNavegador);
            }
        });
        inst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNavegador = new Intent(Intent.ACTION_VIEW, Uri.parse(urlI));
                startActivity(intentNavegador);
            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNavegador = new Intent(Intent.ACTION_VIEW, Uri.parse(urlF));
                startActivity(intentNavegador);
            }
        });

        frameLayoutRegistroC.setVisibility(INVISIBLE);


        txi1 = (TextInputLayout) findViewById(R.id.textInputLayout2);
        txi2 = (TextInputLayout) findViewById(R.id.textInputLayout4);
        btnClose = (Button) findViewById(R.id.btn_close);
        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        WebView wView = new WebView(this);
        myFrag = new FragmentRegistroUsers();
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.frameLayoutRegistro, myFrag).commit();
        awesomevalidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomevalidation.addValidation(this,
                R.id.eT_email,
                Patterns.EMAIL_ADDRESS,
                R.string.invalid_mail);
        awesomevalidation.addValidation(this,
                R.id.et_Pass1,
                "{6,}",
                R.string.invalid_password);

        btnlogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String email = login.getText().toString().trim();
                        String passW = pass.getText().toString();

                        if (!login.getText().toString().equals("") || !pass.getText().toString().equals("") || !pass.getText().toString().equals("") && !login.getText().toString().equals("") || !passW.isEmpty())
                            if (awesomevalidation.validate()) {

                                mAuth.signInWithEmailAndPassword(email, passW).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                        loginOk();
                                        } else {
                                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                            dameToastdeerror(errorCode, view);
                                        }
                                    }

                                });


                            } else {
                                Toast.makeText(LoginActivity.this, "Error usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show();
                                login.setError("");
                                pass.setError("");
                            }
                        else {
                            Toast.makeText(LoginActivity.this, "Error, campos vacios.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 1800);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tr = getSupportFragmentManager().beginTransaction();
                tr.replace(R.id.frameLayoutRegistro, myFrag);
                frameLayoutRegistroC.setVisibility(View.VISIBLE);

                btnRegister.setVisibility(INVISIBLE);
                btnlogin.setVisibility(INVISIBLE);
                txi1.setVisibility(INVISIBLE);
                txi2.setVisibility(INVISIBLE);
                login.setVisibility(INVISIBLE);
                pass.setVisibility(INVISIBLE);
                tr.commit();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayoutRegistroC.setVisibility(INVISIBLE);
                mostrar();
            }
        });

    }

    public void mostrar() {
        frameLayoutRegistroC.setVisibility(INVISIBLE);
        btnRegister.setVisibility(View.VISIBLE);
        btnlogin.setVisibility(View.VISIBLE);
        txi1.setVisibility(View.VISIBLE);
        txi2.setVisibility(View.VISIBLE);
        login.setVisibility(View.VISIBLE);
        pass.setVisibility(View.VISIBLE);
    }

    public void loginOk() {
        Toast.makeText(LoginActivity.this, "Login Correcto", Toast.LENGTH_SHORT).show();
        Intent in = new Intent(LoginActivity.this, MenuPrincipalActivity.class);
        startActivity(in, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle());
        in.putExtra("mail", login.getText().toString());

    }


    public void ocultarTeclado(View view) {
        try {
            // Ocultar el teclado tras introducir un número
            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    private void dameToastdeerror(String error, View v) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":

                Toast.makeText(this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                login.setError("La dirección de correo electrónico está mal formateada.");
                login.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                pass.setError("la contraseña es incorrecta ");
                pass.requestFocus();
                pass.setText("");

                Snackbar s = Snackbar.make(v, "¿Ha olvidado su contraseña?", BaseTransientBottomBar.LENGTH_INDEFINITE);
                s.setAction("Restablecer", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAuth.sendPasswordResetEmail(login.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Correo Enviado", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
                s.show();

                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(this, "Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                login.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                login.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                pass.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                pass.requestFocus();
                break;

        }

    }
}