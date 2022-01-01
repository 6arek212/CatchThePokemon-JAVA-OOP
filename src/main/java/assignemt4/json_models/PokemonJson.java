package assignemt4.json_models;

import assignemt4.models.Pokemon;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PokemonJson {

    public class PokemonJsonInner{
        @SerializedName("pos")
        public String locationString;
        public double value;
        public int type;
    }

    public class PokemonJsonWrapper {
        @SerializedName("Pokemon")
        private PokemonJsonInner pokemon;

        public PokemonJsonInner getPokemon() {
            return pokemon;
        }
    }

    @SerializedName("Pokemons")
    private List<PokemonJsonWrapper> pokemonJsonWrappers;

    public List<PokemonJsonWrapper> getPokemonJsonWrappers() {
        return pokemonJsonWrappers;
    }
}
