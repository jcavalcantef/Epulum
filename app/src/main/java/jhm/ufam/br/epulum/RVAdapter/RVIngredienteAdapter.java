package jhm.ufam.br.epulum.RVAdapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jhm.ufam.br.epulum.Classes.Ingrediente;
import jhm.ufam.br.epulum.R;

public class RVIngredienteAdapter extends RecyclerView.Adapter<RVIngredienteAdapter.IngredienteViewHolder> {

    public static class IngredienteViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView txt_ingred;

        public IngredienteViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv_ingrediente);
            txt_ingred = (TextView)itemView.findViewById(R.id.txt_ingrediente);
        }
    }

    List<Ingrediente> ingredientes;

    public RVIngredienteAdapter(List<Ingrediente> ingredien){
        this.ingredientes = ingredien;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public IngredienteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_ingrediente, viewGroup, false);
        IngredienteViewHolder pvh = new IngredienteViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RVIngredienteAdapter.IngredienteViewHolder receitaViewHolder, int i) {
        receitaViewHolder.txt_ingred.setText("  "+ingredientes.get(i).toString());

    }

    @Override
    public int getItemCount() {
        return ingredientes.size();
    }
}
