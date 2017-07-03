package jhm.ufam.br.epulum.RVAdapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jhm.ufam.br.epulum.R;
import jhm.ufam.br.epulum.Classes.Receita;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ReceitaViewHolder> {

    public static class ReceitaViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView receitaNome;
        ImageView receitaFoto;

        ReceitaViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            receitaNome = (TextView)itemView.findViewById(R.id.person_name);
            receitaFoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }

    List<Receita> receitas;

    public RVAdapter(List<Receita> receitas){
        this.receitas = receitas;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ReceitaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        ReceitaViewHolder pvh = new ReceitaViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ReceitaViewHolder receitaViewHolder, int i) {
        receitaViewHolder.receitaNome.setText(receitas.get(i).getName());
        receitaViewHolder.receitaFoto.setImageResource(receitas.get(i).getPhotoId());
    }

    @Override
    public int getItemCount() {
        return receitas.size();
    }
}
