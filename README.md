# moneropool

A tool for bullying \#monero-pools members.

## Natives Installation

moneropool requires the installation of various native libraries.

### Linux

#### Monero Natives

```bash
# See Monero's README document for required dependencies...
git clone https://github.com/monero-project/monero.git
mkdir monero/build
cd $_
cmake .. -DCMAKE_BUILD_TYPE=Release -DBUILD_SHARED_LIBS=On
cmake --build . -j16
# Only cryptonote_basic and randomx (and their dependencies) are required, but
# it's easier to just grab them all.
find . -name '*.so' -exec sudo cp -v {} /usr/local/lib64/ \;
sudo ldconfig
```

#### moneropool Natives

```bash
# Ensure that JAVA_HOME is set
echo $JAVA_HOME
git clone https://github.com/00-matt/moneropool.git
cd moneropool
make
sudo make install
sudo ldconfig
```
