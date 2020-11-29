package com.example.boleboka

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.exercise_items.view.*

/* Erlend: Ettersom workout- og exersice-list fungerer på samme måte prøvde jeg finne en måte
 * å bruke ett adapter for begge. Dette klarte jeg ikke få til så de har separate adaptere som er
 * helt like.
 */

class AdapterExercise(
    private val exerciseList: List<Exercise_Item>,
    private val listnerExercise: Exercise
) : RecyclerView.Adapter<AdapterExercise.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Erlend: Layoutinflater gjør xlm-filer om til View-objekter
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.exercise_items, parent, false)
        return ViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*
         * Erlend: Blir called hver gang du scroller og nye views kommer inn.
         * Ikke add noe kode i denne, da vil appen bli treg
         * Bruk class ViewHolder
         */
        val currentItem = exerciseList[position]
        val currentRep = currentItem.reps
        val currentSets = currentItem.sets
        holder.name.text = currentItem.name
        holder.reps.text = "Reps: $currentRep"
        holder.sets.text = "Sets: $currentSets"
    }

    override fun getItemCount() = exerciseList.size

    /* Erlend: En "Viewholder" representerer ett punkt i listen
     * Altså den inneholder en instanse av layouten exersice_items.xlm
     * Den inneholder også annen data om dette punktet i listen.
     * Som feks hvor dette punktet er i listen (posisjonen)
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val name: TextView = itemView.name
        val reps: TextView = itemView.reps
        val sets: TextView = itemView.sets

        //Konstruktør
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            /*
             * Erlend: Når du sletter ett item har det en animasjon før det forsvinner.
             * Om en trykker på ett item mens remove-animasjonen kjører vil den ikke ha en posisjon.
             * Derfor må vi sjekke med en if-setning for å unngå at appen krasjer
             */
            if(position != RecyclerView.NO_POSITION) {
                listnerExercise.onExerciseClick(position)
            }
        }
    }

    //Erlend: Brukes for å sende click event til andre plasser, ikke hensiktsmessig å ha det i adapteren
    interface OnItemClickListener{
        fun onExerciseClick(position: Int)
    }
}