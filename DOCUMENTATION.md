# Technická dokumentace – Hra Pexeso pro Android

## Přehled architektury aplikace
Aplikace je postavena na standardní Android architektuře s jednou aktivitou (`MainActivity`). Uživatelské rozhraní je definováno v XML rozvržení, zatímco herní logika, správa stavu a interakce jsou implementovány v jazyce Java.

## Popis tříd a funkcí

### MainActivity.java
Hlavní a jediná třída aplikace, která zajišťuje veškerou funkčnost:
- **`onCreate()`**: Inicializuje UI prvky, načítá nejlepší skóre z `SharedPreferences` a spouští novou hru.
- **`setupGame()`**: 
    - Generuje seznam 16 dvojic čísel (0-15).
    - Náhodně zamíchá karty pomocí `Collections.shuffle()`.
    - Dynamicky vytváří 32 tlačítek v `GridLayout` (4 sloupce, 8 řádků).
- **`onCardClicked(int index)`**:
    - Ošetřuje kliknutí na kartu.
    - Zabraňuje kliknutí na již otočenou nebo zmizelou kartu.
    - Zobrazuje hodnotu karty.
- **`checkMatch()`**:
    - Porovnává dvě naposledy otočené karty.
    - Pokud se shodují, po krátké prodlevě je zneviditelní.
    - Pokud se neshodují, po prodlevě je otočí zpět (změní text na "?").
    - Inkrementuje počítadlo tahů (skóre).
- **`endGame()`**:
    - Kontroluje, zda bylo dosaženo nového nejlepšího skóre (nejnižší počet tahů).
    - Ukládá nejlepší skóre trvale do zařízení.
- **`startNewGame()`**: Resetuje stav hry a vygeneruje novou plochu.

## Uživatelské rozhraní
- **ConstraintLayout**: Kořenový element pro flexibilní umístění prvků.
- **GridLayout**: Použit pro zobrazení sítě 4x8 karet.
- **ScrollView**: Zajišťuje, že herní plocha bude přístupná i na menších displejích.
- **TextView**: Zobrazuje aktuální počet tahů a historicky nejlepší výsledek.

## Použité knihovny a nástroje
- **Android SDK**: Základní vývojový nástroj.
- **androidx.appcompat**: Pro zpětnou kompatibilitu UI prvků.
- **SharedPreferences**: Pro lokální ukládání nejlepšího skóre.
- **Handler**: Pro implementaci časové prodlevy při otáčení karet, aby uživatel viděl výsledek.

## Optimalizace
Aplikace využívá váhy v `GridLayout` a `ScrollView`, což zajišťuje, že se herní plocha přizpůsobí různým velikostem a orientacím obrazovky.
