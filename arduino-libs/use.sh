cd "$(dirname "$0")"

mkdir -p "$HOME/sketchbook/libraries"

for lib in */; do
    ln -s "$(realpath "$lib")"  "$HOME/sketchbook/libraries/"
done
