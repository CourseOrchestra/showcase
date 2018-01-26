define([], function(){

return {
	managerModule: "course/tests/mil01",
	features: [
	{
		name: "Sectors",
		features: [
			{
				type: "Sector",
				center: [39.720278, 43.585278],
				angle1: 200,
				angle2: 230,
				radius: 800000
			},
			{
				type: "Sector",
				center: [39.716667, 47.233333],
				angle1: 60,
				angle2: 90,
				radius: 600000
			},
			{
				type: "Sector",
				center: [39.716667, 47.233333],
				angle1: 110,
				angle2: 130,
				radius: 400000
			},
			{
				type: "Sector",
				center: [39.716667, 47.233333],
				angle1: 150,
				angle2: 180,
				radius: 200000
			},
			{
				type: "Sector",
				center: [39.716667, 47.233333],
				angle1: 200,
				angle2: 230,
				radius: 500000
			},
			{
				type: "Sector",
				center: [39.716667, 47.233333],
				angle1: 300,
				angle2: 340,
				radius: 400000
			}
		]
	},
	{
		name: "Objects",
		features: [
			{
				name: "Ковалевка РЭБ",
				id: "kovalevka",
				type: "Point",
				coords: [39.6835, 47.26612],
				label: "00 ocREB"
			},
			{
				name: "Ростов-на-Дону РЭБ",
				id: "rostov1",
				type: "Point",
				coords: [39.6835, 47.26612],
				label: "00 CKU REB"
			},
			{
				name: "Ростов-на-Дону РЭБ",
				id: "rostov2",
				type: "Point",
				coords: [39.6835, 47.26612],
				label: "av esk RE"
			},
			{
				name: "Зеленчукская СВ",
				id: "zelen1",
				type: "Point",
				coords: [48.57381, 41.16996],
				label: "00 omsbr"
			},
			{
				name: "Зеленчукская РЭБ",
				id: "zelen2",
				type: "Point",
				coords: [48.57381, 41.16996],
				label: "00 orREB"
			},
			{
				name: "Гудаута СВ",
				id: "gud1",
				type: "Point",
				coords: [45.44626, 41.16991],
				label: "00 VB"
			},
			{
				name: "Гудаута РЭБ",
				id: "gud2",
				type: "Point",
				coords: [45.44626, 41.16991],
				label: "00 orREB"
			},
			{
				name: "Майкоп СВ",
				id: "maikop1",
				type: "Point",
				coords: [40.07108, 44.58328],
				label: "00 omsbr"
			},
			{
				name: "Майкоп РЭБ",
				id: "maikop2",
				type: "Point",
				coords: [40.07108, 44.58328],
				label: "00 orREB"
			},
			{
				name: "Буденновск СВ",
				id: "bud1",
				type: "Point",
				coords: [44.18114, 44.77244],
				label: "00 omsbr"
			},
			{
				name: "Буденновск РЭБ",
				id: "bud2",
				type: "Point",
				coords: [44.18114, 44.77244],
				label: "00 orREB"
			},
			{
				name: "Владикавказ РЭБ",
				id: "vlad1",
				type: "Point",
				coords: [44.68370, 42.99133],
				label: "00 obREB"
			},
			{
				name: "Владикавказ СВ",
				id: "vlad2",
				type: "Point",
				coords: [44.68370, 42.99133],
				label: "00 omsbr"
			},
			{
				name: "Владикавказ РЭБ",
				id: "vlad3",
				type: "Point",
				coords: [44.68370, 42.99133],
				label: "00 orREB"
			},
			{
				name: "Шали СВ",
				id: "shali1",
				type: "Point",
				coords: [45.08122, 42.50671],
				label: "00 omsbr"
			},
			{
				name: "Шали РЭБ",
				id: "shali2",
				type: "Point",
				coords: [45.08122, 42.50671],
				label: "00 orREB"
			},
			{
				name: "Калиновская СВ",
				id: "kalin1",
				type: "Point",
				coords: [42.54616, 43.37006],
				label: "00 omsbr"
			},
			{
				name: "Калиновская РЭБ",
				id: "kalin2",
				type: "Point",
				coords: [42.54616, 43.37006],
				label: "00 orREB"
			},
			{
				name: "Буйнакск СВ",
				id: "buin1",
				type: "Point",
				coords: [48.88131, 41.16996],
				label: "00 omsbr"
			},
			{
				name: "Буйнакск РЭБ",
				id: "buin2",
				type: "Point",
				coords: [48.88131, 41.16996],
				label: "00 orREB"
			},
			{
				name: "Борзой СВ",
				id: "borz1",
				type: "Point",
				coords: [43.54116, 43.58492],
				label: "00 omsbr"
			},
			{
				name: "Борзой РЭБ",
				id: "borz2",
				type: "Point",
				coords: [43.54116, 43.58492],
				label: "00 orREB"
			},
			{
				name: "Цхинвал СВ",
				id: "chinv1",
				type: "Point",
				coords: [43.93371, 42.21743],
				label: "00 VB"
			},
			{
				name: "Цхинвал РЭБ",
				id: "chinv2",
				type: "Point",
				coords: [43.93371, 42.21743],
				label: "00 orREB"
			},
			{
				name: "Севастополь РЭБ",
				id: "sev",
				type: "Point",
				coords: [33.6135, 44.64212],
				label: "00 ocREB"
			},
			{
				name: "Новороссийск ВМФ",
				id: "nov1",
				type: "Point",
				coords: [37.75355, 44.81151],
				label: "VMB"
			},
			{
				name: "Новороссийск РЭБ",
				id: "nov2",
				type: "Point",
				coords: [37.75355, 44.81151],
				label: "00 CKU REB"
			},
			{
				name: "Астрахань РЭБ",
				id: "astr",
				type: "Point",
				coords: [48.01117, 46.35301],
				label: "00 CKU REB"
			}
		]
	}
	]

};

});
