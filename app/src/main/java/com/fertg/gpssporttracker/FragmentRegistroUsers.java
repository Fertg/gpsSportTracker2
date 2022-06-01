package com.fertg.gpssporttracker;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.fertg.gpssporttracker.ModeloDatos.Corredor;
import com.fertg.gpssporttracker.ModeloDatos.Organizador;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FragmentRegistroUsers extends Fragment {
    private Button btnGuardar,btnLimpiar;
    private Fragment myFrag;
    private FragmentTransaction tr;
    private EditText user, pass, pass1,nameUser;
    private FirebaseAuth auth;
    private CheckBox chbxCondition;
    private AwesomeValidation awesomevalidation;
    private LoginActivity l = new LoginActivity();
    private InputMethodManager imm;
    private DatabaseReference mDatabase;
    private String typeR="user";
    public FragmentRegistroUsers() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragmentRegistroUsers newInstance(String param1, String param2) {
        FragmentRegistroUsers fragment = new FragmentRegistroUsers();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        if (getArguments() != null) {

        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registro_users, container, false);
        btnGuardar = (Button) v.findViewById(R.id.btn_registro);
        btnLimpiar = (Button) v.findViewById(R.id.btn_limpiarRe);
        user = (EditText) v.findViewById(R.id.eT_email);
        nameUser = (EditText) v.findViewById(R.id.eT_name);
        pass = (EditText) v.findViewById(R.id.et_Pass1);
        pass1 = (EditText) v.findViewById(R.id.et_Pass2);
        auth = FirebaseAuth.getInstance();

        awesomevalidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomevalidation.addValidation(getActivity(),
                R.id.eT_email,
                Patterns.EMAIL_ADDRESS,
                R.string.invalid_mail);
        awesomevalidation.addValidation(getActivity(),
                R.id.et_Pass1,
                "{6,}",
                R.string.invalid_password);

        chbxCondition = (CheckBox) v.findViewById(R.id.checkBox2);

        myFrag = new FragmentRegistroUsers();
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass.setText("");
                pass1.setText("");
                user.setText("");
                nameUser.setText("");
                chbxCondition.clearFocus();
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrameLayout fragment = getActivity().findViewById(R.id.frameLayoutRegistro);
                Button btnlogin = getActivity().findViewById(R.id.btnLogin);
                Button btnRegister = getActivity().findViewById(R.id.btnRegistro);
                EditText login = getActivity().findViewById(R.id.etUsu);
                EditText userName = getActivity().findViewById(R.id.eT_name);
                EditText passf = getActivity().findViewById(R.id.etPass);
                TextInputLayout txi1 = getActivity().findViewById(R.id.textInputLayout2);
                TextInputLayout txi2 = getActivity().findViewById(R.id.textInputLayout4);


                    if (user.getText().toString().equals("") &&
                            pass.getText().toString().equals("") && pass1.getText().toString().equals("") || pass1.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Error, campos vacios", Toast.LENGTH_SHORT).show();
                    } else {
                        if (pass.getText().toString().equals(pass1.getText().toString())) {
                            String nombreC = userName.getText().toString();                            String email = user.getText().toString();
                            String pass12 = pass.getText().toString();

                            if (awesomevalidation.validate()) {
                                auth.createUserWithEmailAndPassword(email, pass12).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {
                                            if (chbxCondition.isChecked()) {
                                                String id= auth.getCurrentUser().getUid();
                                                Corredor corredor = new Corredor();
                                                corredor.setEmail(email);
                                                corredor.setName(nombreC);
//                                                TYPE 1:ORGANIZADOR
                                                corredor.setType(1);
                                                pass.setText("");
                                                pass1.setText("");
                                                user.setText("");
                                                nameUser.setText("");
                                                mDatabase.child("Users").child(id).setValue(corredor);


                                            }else{
                                                String id= auth.getCurrentUser().getUid();
                                                Corredor corredor = new Corredor();
                                                corredor.setEmail(email);
                                                corredor.setName(nombreC);
//                                                TYPE 0:USER
                                                corredor.setType(0);
                                                pass.setText("");
                                                pass1.setText("");
                                                user.setText("");
                                                nameUser.setText("");
                                                mDatabase.child("Users").child(id).setValue(corredor);
                                            }

                                            Toast.makeText(getContext(), "Usuario registrado", Toast.LENGTH_SHORT).show();



                                            //Ocultar botones y fragment
                                            fragment.setVisibility(View.INVISIBLE);
                                            btnRegister.setVisibility(View.VISIBLE);
                                            btnlogin.setVisibility(View.VISIBLE);
                                            txi1.setVisibility(View.VISIBLE);
                                            txi2.setVisibility(View.VISIBLE);
                                            login.setVisibility(View.VISIBLE);
                                            passf.setVisibility(View.VISIBLE);

                                        } else {
                                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                            dameToastdeerror(errorCode);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Error campos vacios", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getContext(), "Error las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        }
                    }


            }
        });
        return v;
    }

    private void dameToastdeerror(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(getActivity(), "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(getActivity(), "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(getActivity(), "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(getActivity(), "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                user.setError("La dirección de correo electrónico está mal formateada.");
                user.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(getActivity(), "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                pass.setError("la contraseña es incorrecta ");
                pass.requestFocus();
                pass.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(getActivity(), "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(getActivity(), "Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(getActivity(), "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(getActivity(), "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                user.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                user.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(getActivity(), "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(getActivity(), "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(getActivity(), "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(getActivity(), "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(getActivity(), "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(getActivity(), "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(getActivity(), "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                pass.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                pass.requestFocus();
                break;

        }

    }





}