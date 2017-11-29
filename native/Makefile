java_incl = $(JAVA_HOME)/include
lib_name = contraction_order_finder_BFS
deps = src/helpers/bitmask.c src/$(lib_name)/ord.c
out = ../target/native/
default:
	mkdir -p $(out) && \
	gcc -Wall -shared -fpic -I$(java_incl) -I$(java_incl)/linux -I$(java_incl)/darwin \
		$(shell pkg-config --cflags --libs glib-2.0) \
		-o $(out)/$(lib_name).so src/$(lib_name).c $(deps)
