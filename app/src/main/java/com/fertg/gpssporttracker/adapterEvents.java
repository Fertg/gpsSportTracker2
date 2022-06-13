package com.fertg.gpssporttracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fertg.gpssporttracker.ModeloDatos.Evento;

import java.util.List;
//Adapter para inflar los RV de misEvents
public class adapterEvents extends RecyclerView.Adapter<adapterEvents.eventosViewHolder>{
    List<Evento> eventos;

    public adapterEvents(List<Evento> eventos) {
        this.eventos = eventos;
    }

    @NonNull
    @Override
    public eventosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
  View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowrecyclerevent,parent,false);
        eventosViewHolder holder = new eventosViewHolder(v);
        return holder;
    }
//Setter del rv
    @Override
    public void onBindViewHolder(@NonNull eventosViewHolder holder, int position) {
Evento eventoObj = eventos.get(position);
holder.nombreTV.setText("Nombre: "+eventoObj.getNombreEvento());
holder.lugarTV.setText("Lugar: "+eventoObj.getLugarEvento());
holder.modalidadTV.setText("Modalidad: "+eventoObj.getModalidad());
holder.numeroTV.setText("Número: "+eventoObj.getNumeroCorredores());
holder.fechaTV.setText("Fecha: "+eventoObj.getFechaEvento());
holder.codeTV.setText("Código: "+eventoObj.getCodigoEvento());

    }
//Obtener el numero de cardsviews
    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public static class eventosViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTV, lugarTV, modalidadTV, codeTV, fechaTV, numeroTV;
// referencias  del activity
        public eventosViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTV = (TextView) itemView.findViewById(R.id.tv_nombreRv);
            lugarTV = (TextView) itemView.findViewById(R.id.tv_localidad);
            modalidadTV = (TextView) itemView.findViewById(R.id.tv_modalidad);
            codeTV = (TextView) itemView.findViewById(R.id.tv_code);
            fechaTV = (TextView) itemView.findViewById(R.id.tv_fecha);
            numeroTV = (TextView) itemView.findViewById(R.id.tv_numero);





        }
    }

}
