CXXFLAGS = -std=c++11 -Wall -Wextra -O2 -fPIC
CPPFLAGS = -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux
LDFLAGS = -shared
LDLIBS = -lcryptonote_basic

DESTDIR =
PREFIX = /usr/local
LIBDIR = $(PREFIX)/lib64

.PHONY: all
all: libmoneropool.so

libmoneropool.so: src/main/cpp/uk_offtopica_moneropool_util_NativeUtils.cpp
	$(CXX) $(CXXFLAGS) $(CPPFLAGS) $(LDFLAGS) -o $@ $< $(LDLIBS)

.PHONY: install
install: libmoneropool.so
	install -m0755 libmoneropool.so $(LIBDIR)/

.PHONY: clean
clean:
	$(RM) libmoneropool.so
