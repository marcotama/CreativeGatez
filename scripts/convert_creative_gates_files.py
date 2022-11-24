import os
import json

in_dir = '/path/to/creativegates_ugate@default'
out_file = '/path/to/gates.json'

def is_frame(t, gate_coords):
    x, y, z = t
    relative = {
        (x + 1, y, z),
        (x - 1, y, z),
        (x, y + 1, z),
        (x, y - 1, z),
        (x, y, z + 1),
        (x, y, z - 1),
    }
    not_in_gate = {coords for coords in relative if coords not in gate_coords}
    return len(not_in_gate) > 2

def dict_to_tuple(c):
    return (c['bx'], c['by'], c['bz'])

def dicts_to_tuples(coords):
    return {dict_to_tuple(c) for c in coords}


out_gates_data = {'gates': []}
for filename in os.listdir(in_dir):
    if not filename.endswith('.json'):
        continue
    full_path = os.path.join(directory, filename)
    if os.path.isfile(full_path):
        with open(full_path, 'r') as f:
            cgs_data = json.load(f)
        coords_as_tuples = dicts_to_tuples(cgs_data['coords'])
        out_gate_data = {
            'id': None,
            'world': cgs_data['exit']['w'], # assumption: the world is the same for entrance and exit
            'networkId': cgs_data['networkId'],
            'exit': {
                'world': cgs_data['exit']['w'],
                'location': {
                    'blockX': cgs_data['exit']['lx'],
                    'blockY': cgs_data['exit']['ly'],
                    'blockZ': cgs_data['exit']['lz'],
                },
                'heading': {
                    'pitch': cgs_data['exit']['p'],
                    'yaw': cgs_data['exit']['y'],
                }
            },
            'creatorId': cgs_data['creatorId'],
            'creationTimeMillis': cgs_data['createdMillis'],
            'restricted': cgs_data['restricted'],
            'enterEnabled': cgs_data['enterEnabled'],
            'exitEnabled': cgs_data['exitEnabled'],
            'frameCoords': [
                {
                    'blockX': c['bx'],
                    'blockY': c['by'],
                    'blockZ': c['bz'],
                }
                for c in cgs_data['coords']
                if is_frame(dict_to_tuple(c), coords_as_tuples)
            ],
            'portalCoords': [
                {
                    'blockX': c['bx'],
                    'blockY': c['by'],
                    'blockZ': c['bz'],
                }
                for c in cgs_data['coords']
                if not is_frame(dict_to_tuple(c), coords_as_tuples)
            ],
        }
        out_gates_data['gates'].append(out_gate_data)
with open(out_file, 'w') as f:
    json.dump(out_gates_data, f, indent=4)