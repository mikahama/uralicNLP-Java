# UralicNLP - Multilingual Natural Language Processing for Java

Note: this library is still under construction

UralicNLP can produce **morphological analyses**, **generate morphological forms**, **lemmatize words** and **give lexical information** about words in Uralic and other languages. The languages we support include the following languages: Finnish, Russian, German, English, Norwegian, Swedish, Arabic, Ingrian, Meadow & Eastern Mari, Votic, Olonets-Karelian, Erzya, Moksha, Hill Mari, Udmurt, Tundra Nenets, Komi-Permyak, North Sami, South Sami and Skolt Sami. The functionality originates mainly in FST tools and dictionaries developed in the [GiellaLT infrastructure](https://giellalt.uit.no/) and [Apertium](https://github.com/apertium). Currently, UralicNLP uses nightly builds for most of the supported languages.

# Installation

More instructions after the first release...

If you want to use the Constraint Grammar features (*from uralicNLP.cg3 import Cg3*), you will also need to [install VISL CG-3](https://mikalikes.men/how-to-install-visl-cg3-on-mac-windows-and-linux/).

## Download Models

In order to use any of the language specific features, you will need to download the models for each language by passing the ISO code of the language to the *download* method:

	import com.rootroo.uralicnlp.UralicApi

	UralicApi api = new UralicApi();
	api.download("fin")

The models will be downloaded to *.uralicnlp* folder in your home directory.

# Tokenization

TBA

# Lemmatization

To lemmatize a single word, use the *lemmatize* method. This will produce a list of all the possible lemmas.

	import com.rootroo.uralicnlp.UralicApi

	UralicApi api = new UralicApi();
	System.out.println(api.lemmatize("voin", "fin"));
	>> [voi, vuo, voida]

To mark word boundaries in compound words, pass an additional *true* to the *lemmatize* method:

 	import com.rootroo.uralicnlp.UralicApi

	UralicApi api = new UralicApi();
	System.out.println(api.lemmatize("luutapiiri", "fin", true)));
	>> [luu|tapiiri, luuta|piiri]

# Morphology

To analyze the morpholgy including the part-of-speech of a given word, use the *analyze* method. This will return all the possible morphological interpretations for the input word:

 	import com.rootroo.uralicnlp.UralicApi

	UralicApi api = new UralicApi();
	HashMap<String, Float> results = api.analyze("voin", "fin");
	for(String s : results.keySet()){
		System.out.println(s);
	}

	>>voi+N+Sg+Gen
	>>vuo+N+Pl+Ins
	>>voida+V+Act+Ind+Prt+Sg1
	>>voi+N+Pl+Ins
	>>voida+V+Act+Ind+Prs+Sg1

The result is a HashMap where the keys are morphological readings and the values are the weights (NB most of the models do not have weights).

You can also inflect words by using the *generate* method:

 	import com.rootroo.uralicnlp.UralicApi

	UralicApi api = new UralicApi();
	HashMap<String, Float> results = api.generate("voida+V+Act+Ind+Prt+Sg1", "fin");
	for(String s : results.keySet()){
		System.out.println(s);
	}
	>>voin

The output is a similar HashMap as in the case of *analyze*.

# Business solutions

<img src="https://rootroo.com/cropped-logo-01-png/" alt="Rootroo logo" width="128px" height="128px">

When your NLP needs grow out of what UralicNLP can provide, we have your back! [Rootroo offers consulting related to a variety of NLP tasks](https://rootroo.com/). We have a strong academic background in the state-of-the-art AI solutions for every NLP need. Don't hesitate to contact us, regardless of the task even beyond Uralic languages!

# Cite

If you use UralicNLP in an academic publication, please cite it as follows:

Hämäläinen, Mika. (2019). UralicNLP: An NLP Library for Uralic Languages. Journal of open source software, 4(37), [1345]. https://doi.org/10.21105/joss.01345

    @article{uralicnlp_2019, 
        title={{UralicNLP}: An {NLP} Library for {U}ralic Languages},
        DOI={10.21105/joss.01345}, 
        journal={Journal of Open Source Software}, 
        author={Mika Hämäläinen}, 
        year={2019}, 
        volume={4},
        number={37},
        pages={1345}
    }
