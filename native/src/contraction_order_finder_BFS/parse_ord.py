from pprint import pprint


def parse(order, tensor_cnt, idx=0):
    if order[idx] < tensor_cnt:
        return order[idx]
    else:
        return {
            "left": parse(order, tensor_cnt, idx + 1),
            "right": parse(order, tensor_cnt, order[idx] - tensor_cnt)
        }

pprint(parse([5, 2, 7, 0, 1], 3))
