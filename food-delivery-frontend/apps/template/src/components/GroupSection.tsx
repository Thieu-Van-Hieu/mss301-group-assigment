import React from 'react';

interface GroupSectionProps {
	id: string;
	number: number;
	title: string;
	children: React.ReactNode;
}

export const GroupSection: React.FC<GroupSectionProps> = ({id, number, title, children}) => {
	return (
		<section id={id} className="space-y-6 scroll-mt-24">
			<div className="flex items-center gap-3 border-b-2 border-slate-200 pb-3">
        <span
			className="w-8 h-8 rounded-xl bg-orange-500 text-white font-black text-sm flex items-center justify-center shadow-[0_3px_0_#bd2d00]">
          {number}
        </span>
				<h3 className="text-xl font-black text-slate-800">{title}</h3>
			</div>
			{children}
		</section>
	);
};