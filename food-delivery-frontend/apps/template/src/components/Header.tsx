import React from 'react';

export const Header: React.FC = () => {
	return (
		<header
			className="sticky top-0 z-50 bg-white/90 backdrop-blur-md border-b-2 border-slate-200 shadow-[0_4px_0_#e2e8f0]">
			<div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
				<div className="flex items-center justify-between h-20">
					<div className="flex items-center gap-3">
						<div
							className="w-11 h-11 bg-orange-500 rounded-2xl flex items-center justify-center text-white text-xl font-black shadow-[0_4px_0_#bd2d00] active:translate-y-1 active:shadow-none transition-all cursor-pointer">
							UI
						</div>
						<div>
							<h1 className="text-lg font-black text-slate-800 leading-none">3D Tactile UI</h1>
							<p className="text-[11px] font-bold text-slate-400 mt-1 uppercase tracking-wider">Component
								System</p>
						</div>
					</div>

					<nav
						className="hidden lg:flex items-center gap-1 text-xs font-extrabold uppercase tracking-wider text-slate-500">
						<a href="#g1"
						   className="px-3 py-2 rounded-xl hover:bg-slate-100 hover:text-slate-900 transition-all">G1:
							General</a>
						<a href="#g2"
						   className="px-3 py-2 rounded-xl hover:bg-slate-100 hover:text-slate-900 transition-all">G2:
							Inputs</a>
						<a href="#g3"
						   className="px-3 py-2 rounded-xl hover:bg-slate-100 hover:text-slate-900 transition-all">G3:
							Data</a>
						<a href="#g4"
						   className="px-3 py-2 rounded-xl hover:bg-slate-100 hover:text-slate-900 transition-all">G4:
							Nav</a>
						<a href="#g5"
						   className="px-3 py-2 rounded-xl hover:bg-slate-100 hover:text-slate-900 transition-all">G5:
							Overlays</a>
						<a href="#g6"
						   className="px-3 py-2 rounded-xl hover:bg-slate-100 hover:text-slate-900 transition-all">G6:
							Layout</a>
						<a href="#g7"
						   className="px-3 py-2 rounded-xl hover:bg-slate-100 hover:text-slate-900 transition-all">G7:
							Doc Kit</a>
					</nav>

					<div className="flex items-center gap-3">
						<button
							className="relative rounded-2xl font-bold text-slate-600 text-xs py-2.5 px-4 bg-white border-2 border-slate-200 shadow-[0_3px_0_#cbd5e1] hover:bg-slate-50 active:translate-y-0.5 active:shadow-none transition-all flex items-center gap-2">
							<i className="fa-solid fa-magnifying-glass text-slate-400"></i>
							<span>Search</span>
							<kbd className="px-1.5 py-0.5 bg-slate-100 border border-slate-300 rounded-md text-[10px]">Ctrl
								K</kbd>
						</button>
					</div>
				</div>
			</div>
		</header>
	);
};