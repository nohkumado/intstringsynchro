# intstringsynchro
A small android helper to keep the string.xml ressources in sync 
between translations

at the moment i found 2 projects that try to chaieve this, but both are 
directed to commercial translators, who want to translate the stuff 
that wasn't translated, want time accounting and that sort of stuff...

as a dev, i just want a table with the available tokens and see where a
translation is missing...

just starting BTW...

thanks to https://android-arsenal.com/details/1/3950 for the diretory picker

BTW to validate a field you have to hit the enter key, otherwise the input is not taken, even if it is still written there
its a "feature" at least on my kbd the IME_ACTION_DONE is not triggered when i hit the TAB key...

to use it include the instringsynchro jar into your path, add the 


import com.nohkumado.intstringsynchro.*;


create a tablefragment:

protected StringXmlTableFrag tokenTable;

an in MainActivity create do:

   // find the retained fragment on activity restarts
    FragmentManager fm = getFragmentManager();
    tokenTable = (StringXmlTableFrag) fm.findFragmentByTag("data");
    // create the fragment and data the first time
    if (tokenTable == null)
    {
      // add the fragment
      tokenTable = new StringXmlTableFrag(this);
      fm.beginTransaction().add(tokenTable, "data").replace(R.id.table, tokenTable).commit();
    }

