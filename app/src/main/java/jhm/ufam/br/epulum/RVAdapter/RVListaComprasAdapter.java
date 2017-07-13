package jhm.ufam.br.epulum.RVAdapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jhm.ufam.br.epulum.Classes.Ingrediente;
import jhm.ufam.br.epulum.Classes.ListaCompras;
import jhm.ufam.br.epulum.R;

public class RVListaComprasAdapter extends RecyclerView.Adapter<RVListaComprasAdapter.ListaComprasViewHolder> {

    public static class ListaComprasViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView txt_ingred;

        public ListaComprasViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv_ingrediente);
            txt_ingred = (TextView)itemView.findViewById(R.id.txt_ingrediente);
        }
    }

    List<ListaCompras> ingredientes;

    public RVListaComprasAdapter(List<ListaCompras> ingredien){
        this.ingredientes = ingredien;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ListaComprasViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_ingrediente, viewGroup, false);
        ListaComprasViewHolder pvh = new ListaComprasViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RVListaComprasAdapter.ListaComprasViewHolder receitaViewHolder, int i) {
        receitaViewHolder.txt_ingred.setText("  "+ingredientes.get(i).getNome());

    }

    @Override
    public int getItemCount() {
        return ingredientes.size();
    }
}

