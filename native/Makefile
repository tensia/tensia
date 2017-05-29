java_incl = $(shell /usr/libexec/java_home)/include
lib_name = BFS_contraction_order
deps = src/helpers/bitmask.c src/$(lib_name)/ord.c
default:
	gcc -Wall -shared -fpic -I$(java_incl) -I$(java_incl)/darwin \
		$(shell pkg-config --cflags --libs glib-2.0) \
		-o build/$(lib_name).so src/$(lib_name).c $(deps)
